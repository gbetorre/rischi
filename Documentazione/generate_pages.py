#!/usr/bin/env python3
'''
This script converts a docx document into a bunch of html files, one for each section found in the document.
It requires pandoc installed on the system. If you don't have pandoc, you can install it using:
$sudo apt install pandoc (under a Debian/Ubuntu derivative)
Usage of this script:
[python3] generate_pages.py Analisi.docx --title "ROL - Documentazione tecnica convertita da DOCX" --out output/site-build
'''
import argparse, shutil, subprocess, sys, tempfile, re
from pathlib import Path

SECTION_RE = re.compile(r'^(#{1,6})\s+(.*)$', re.M)

def run(cmd, cwd=None):
    subprocess.run(cmd, cwd=cwd, check=True)

def have(cmd):
    return shutil.which(cmd) is not None

def pandoc_convert(src, dest_md, media_dir):
    run([
        'pandoc', '-s', '--wrap=none',
        '--extract-media', str(media_dir),
        '-t', 'gfm', str(src), '-o', str(dest_md)
    ])

def split_markdown(md_text):
    matches = list(SECTION_RE.finditer(md_text))
    if not matches:
        return [('index', md_text)]
    chunks = []
    for i, m in enumerate(matches):
        start = m.start()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(md_text)
        title = m.group(2).strip()
        #slug = re.sub(r'[^a-zA-Z0-9]+', '-', title.lower()).strip('-') or f'section-{i+1}'
        ''' The line above generates errors, like:
        OSError: [Errno 36] File name too long: '/tmp/tmpuwd87m6m/all-inizio-dell-analisi-si-tentato-a-lungo-di-rendere-la-mappatura-dei-processi-
        finalizzata-alla-valutazione-del-rischio-cio-la-mappatura-realizzata-tramite-questo-progetto-rol-compatibile-con-la-mappatura-gi-esistente-
        realizzata-a-fini-di-efficientamento-a-tal-scopo-sono-stati-anche-predisposti-degli-specifici-connettori.md'
        So we use the following, instead: slug = f"section-{i+1}" '''
        slug = f"section-{i+1}"
        chunks.append((slug, md_text[start:end].strip() + '\n'))
    pre = md_text[:matches[0].start()].strip()
    if pre:
        chunks.insert(0, ('index', pre + '\n'))
    return chunks

def md_to_html(md_path, html_path):
    run(['pandoc', '-s', str(md_path), '-t', 'html5', '--mathjax', '-o', str(html_path)])

def build_site(source, workdir, title):
    source = Path(source)
    workdir = Path(workdir)
    site = workdir / 'site'
    site.mkdir(parents=True, exist_ok=True)
    tmp = workdir / 'tmp'
    tmp.mkdir(exist_ok=True)
    md = tmp / 'document.md'
    media = tmp / 'media'
    media.mkdir(exist_ok=True)

    if source.suffix.lower() in {'.docx', '.odt'}:
        pandoc_convert(source, md, media)
    else:
        raise SystemExit('Unsupported source type. Use .docx or .odt.')

    md_text = md.read_text(encoding='utf-8')
    chunks = split_markdown(md_text)

    assets = site / 'assets'
    assets.mkdir(exist_ok=True)
    if media.exists():
        shutil.copytree(media, assets / 'media', dirs_exist_ok=True)

    nav_items = []
    for slug, _ in chunks:
        label = slug.replace('-', ' ').title()
        nav_items.append(f'<li><a href="{slug}.html">{label}</a></li>')

    (site / 'style.css').write_text(
        'body{font-family:system-ui,sans-serif;max-width:980px;margin:2rem auto;padding:0 1rem;line-height:1.6}'
        'nav ul{list-style:none;padding:0}nav li{display:inline-block;margin-right:1rem}'
        'img{max-width:100%;height:auto}',
        encoding='utf-8'
    )

    (site / 'index.html').write_text(
        f'<!doctype html><html><head><meta charset="utf-8"><title>{title}</title>'
        f'<link rel="stylesheet" href="style.css"></head><body>'
        f'<h1>{title}</h1><nav><ul>{"".join(nav_items)}</ul></nav>'
        f'<p>Generated from {source.name}</p></body></html>',
        encoding='utf-8'
    )

    with tempfile.TemporaryDirectory() as td:
        td = Path(td)
        for slug, content in chunks:
            mdf = td / f'{slug}.md'
            mdf.write_text(content, encoding='utf-8')
            htmlf = site / f'{slug}.html'
            md_to_html(mdf, htmlf)
            txt = htmlf.read_text(encoding='utf-8')
            txt = txt.replace('<body>', '<body><p><a href="index.html">Home</a></p>')
            htmlf.write_text(txt, encoding='utf-8')

    return site

if __name__ == '__main__':
    ap = argparse.ArgumentParser()
    ap.add_argument('source')
    ap.add_argument('--title', default='Analysis Site')
    ap.add_argument('--out', default='site-build')
    args = ap.parse_args()

    if not have('pandoc'):
        raise SystemExit('pandoc not found in PATH')

    site = build_site(args.source, args.out, args.title)
    print(site)