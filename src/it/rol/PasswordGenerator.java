/*
 *   Process Mapping Software: Modulo Applicazione web per la visualizzazione
 *   delle schede di indagine su allocazione risorse dell'ateneo,
 *   per la gestione dei processi on line (pms).
 *
 *   Process Mapping Software (pms)
 *   web applications to publish, and manage,
 *   processes, assessment and skill information.
 *   Copyright (C) renewed 2022 Giovanroberto Torre
 *   all right reserved
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA<br>
 *
 *   Giovanroberto Torre <gianroberto.torre@gmail.com>
 *   Universita' degli Studi di Verona
 *   Via Dell'Artigliere, 8
 *   37129 Verona (Italy)
 */

package it.rol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * <p>PasswordGenerator.java &egrave; una classe di utilit&agrave;
 * che pu&ograve; essere utilizzata per generare automaticamente password
 * rispondenti ai criteri specificati.
 * <ul>
 * <li>This class is immutable</li>
 * <li>It uses the builder pattern</li>
 * <li>It doesn't support extension</li>
 * </ul>
 * <p>This specific class is released by the author in the Public Domain.<br />
 * In this specific application, pol, altough, this public license is been
 * wrapped by the GNU General Public License (GNU GPL) according to the
 * principles applied to the most of our other sources.</p>
 *
 * @author <a href="https://stackoverflow.com/users/1123501/george-siggouroglou">George Siggouroglou</a>
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public final class PasswordGenerator {

    //private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    //private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    //private static final String DIGITS = "0123456789";
    //private static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
    private static final String LOWER = "abcdefghijklmnpqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNPQRSTUVWXYZ";
    private static final String DIGITS = "123456789";
    private static final String PUNCTUATION = "!@#$%&()+-=[]?><";
    private boolean useLower;
    private boolean useUpper;
    private boolean useDigits;
    private boolean usePunctuation;


    private PasswordGenerator() {
        throw new UnsupportedOperationException("Empty constructor is not supported.");
    }


    private PasswordGenerator(PasswordGeneratorBuilder builder) {
        this.useLower = builder.useLower;
        this.useUpper = builder.useUpper;
        this.useDigits = builder.useDigits;
        this.usePunctuation = builder.usePunctuation;
    }


    public static class PasswordGeneratorBuilder {

        private boolean useLower;
        private boolean useUpper;
        private boolean useDigits;
        private boolean usePunctuation;


        public PasswordGeneratorBuilder() {
            this.useLower = false;
            this.useUpper = false;
            this.useDigits = false;
            this.usePunctuation = false;
        }


        /**
         * Set true in case you would like to include lower characters
         * (abc...xyz). Default false.
         *
         * @param useLower true in case you would like to include lower
         * characters (abc...xyz). Default false.
         * @return the builder for chaining.
         */
        public PasswordGeneratorBuilder useLower(boolean useLower) {
            this.useLower = useLower;
            return this;
        }


        /**
         * Set true in case you would like to include upper characters
         * (ABC...XYZ). Default false.
         *
         * @param useUpper true in case you would like to include upper
         * characters (ABC...XYZ). Default false.
         * @return the builder for chaining.
         */
        public PasswordGeneratorBuilder useUpper(boolean useUpper) {
            this.useUpper = useUpper;
            return this;
        }


        /**
         * Set true in case you would like to include digit characters (123..).
         * Default false.
         *
         * @param useDigits true in case you would like to include digit
         * characters (123..). Default false.
         * @return the builder for chaining.
         */
        public PasswordGeneratorBuilder useDigits(boolean useDigits) {
            this.useDigits = useDigits;
            return this;
        }


        /**
         * Set true in case you would like to include punctuation characters
         * (!@#..). Default false.
         *
         * @param usePunctuation true in case you would like to include
         * punctuation characters (!@#..). Default false.
         * @return the builder for chaining.
         */
        public PasswordGeneratorBuilder usePunctuation(boolean usePunctuation) {
            this.usePunctuation = usePunctuation;
            return this;
        }


        /**
         * Get an object to use.
         *
         * @return the {@link gr.idrymavmela.business.lib.PasswordGenerator}
         * object.
         */
        public PasswordGenerator build() {
            return new PasswordGenerator(this);
        }
    }


    /**
     * This method will generate a password depending the use* properties you
     * define. It will use the categories with a probability. It is not sure
     * that all of the defined categories will be used.
     *
     * @param length the length of the password you would like to generate.
     * @return a password that uses the categories you define when constructing
     * the object with a probability.
     */
    public String generate(int length) {
        // Argument Validation.
        if (length <= 0) {
            return Constants.VOID_STRING;
        }
        // Variables.
        StringBuilder password = new StringBuilder(length);
        Random random = new Random(System.nanoTime());
        // Collect the categories to use.
        List<String> charCategories = new ArrayList<>(4);
        if (useLower) {
            charCategories.add(LOWER);
        }
        if (useUpper) {
            charCategories.add(UPPER);
        }
        if (useDigits) {
            charCategories.add(DIGITS);
        }
        if (usePunctuation) {
            charCategories.add(PUNCTUATION);
        }
        // Build the password.
        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
            password.append(charCategory.charAt(position));
        }
        return new String(password);
    }

}
