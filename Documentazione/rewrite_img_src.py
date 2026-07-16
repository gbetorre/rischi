'''
This script changes the path of all the images in an html document, from a given path to another one.
Usage:
To convert A SINGLE FILE (index.html) run:
python3 -c "from rewrite_img_src import rewrite_img_src; rewrite_img_src('output/site-build/site/index.html', 'output/site-build/tmp/media', 'assets/media')"

To convert ALL HTML FILES in dir:
python3 -c "from pathlib import Path; from rewrite_img_src import rewrite_img_src; [rewrite_img_src(str(p), 'output/site-build/tmp/media', 'assets/media') for p in Path('output/site-build/site').glob('*.html')]"
'''
from bs4 import BeautifulSoup
from pathlib import Path

# Usage: rewrite_img_src(html_file, wrong_path, correct_path)
# Params:
# html_path: the file to change
# old_root: the incorrect path of img resource, to change
# new_root: the correct one
def rewrite_img_src(html_path, old_root, new_root):
    html_path = Path(html_path)
    soup = BeautifulSoup(html_path.read_text(encoding='utf-8'), 'html.parser')

    for img in soup.find_all('img', src=True):
        src = img['src'].replace('\\', '/')
        old_root = old_root.replace('\\', '/').rstrip('/')
        new_root = new_root.replace('\\', '/').rstrip('/')

        if src.startswith(old_root):
            img['src'] = src.replace(old_root, new_root, 1)
        elif src.startswith('media/'):
            img['src'] = f"{new_root}/{src}"

    html_path.write_text(str(soup), encoding='utf-8')
    