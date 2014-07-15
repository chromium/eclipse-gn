/*
 * Copyright (c) 2014, The Chromium Authors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chromium.gnsupport.internal.ui;

import org.chromium.gnsupport.internal.ui.editor.GnColorConstants;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

public class GnPreferenceConstants extends PreferenceConstants {

  public static final String EDITOR_STRING_COLOR = GnColorConstants.GN_STRING;
  public final static String EDITOR_STRING_BOLD = GnColorConstants.GN_STRING + EDITOR_BOLD_SUFFIX;
  public final static String EDITOR_STRING_ITALIC = GnColorConstants.GN_STRING
      + EDITOR_ITALIC_SUFFIX;
  public final static String EDITOR_STRING_STRIKETHROUGH = GnColorConstants.GN_STRING
      + EDITOR_STRIKETHROUGH_SUFFIX;
  public final static String EDITOR_STRING_UNDERLINE = GnColorConstants.GN_STRING
      + EDITOR_UNDERLINE_SUFFIX;

  public static final String EDITOR_NUMBER_COLOR = GnColorConstants.GN_NUMBER;
  public final static String EDITOR_NUMBER_BOLD = GnColorConstants.GN_NUMBER + EDITOR_BOLD_SUFFIX;
  public final static String EDITOR_NUMBER_ITALIC = GnColorConstants.GN_NUMBER
      + EDITOR_ITALIC_SUFFIX;
  public final static String EDITOR_NUMBER_STRIKETHROUGH = GnColorConstants.GN_NUMBER
      + EDITOR_STRIKETHROUGH_SUFFIX;
  public final static String EDITOR_NUMBER_UNDERLINE = GnColorConstants.GN_KEYWORD
      + EDITOR_UNDERLINE_SUFFIX;

  public final static String EDITOR_SINGLE_LINE_COMMENT_COLOR = GnColorConstants.GN_COMMENT;
  public final static String EDITOR_SINGLE_LINE_COMMENT_BOLD = GnColorConstants.GN_COMMENT
      + EDITOR_BOLD_SUFFIX;
  public final static String EDITOR_SINGLE_LINE_COMMENT_ITALIC = GnColorConstants.GN_COMMENT
      + EDITOR_ITALIC_SUFFIX;
  public final static String EDITOR_SINGLE_LINE_COMMENT_STRIKETHROUGH = GnColorConstants.GN_COMMENT
      + EDITOR_STRIKETHROUGH_SUFFIX;
  public final static String EDITOR_SINGLE_LINE_COMMENT_UNDERLINE = GnColorConstants.GN_KEYWORD
      + EDITOR_UNDERLINE_SUFFIX;

  public static final String EDITOR_KEYWORD_COLOR = GnColorConstants.GN_KEYWORD;
  public final static String EDITOR_KEYWORD_BOLD = GnColorConstants.GN_KEYWORD + EDITOR_BOLD_SUFFIX;
  public final static String EDITOR_KEYWORD_ITALIC = GnColorConstants.GN_KEYWORD
      + EDITOR_ITALIC_SUFFIX;
  public final static String EDITOR_KEYWORD_STRIKETHROUGH = GnColorConstants.GN_KEYWORD
      + EDITOR_STRIKETHROUGH_SUFFIX;
  public final static String EDITOR_KEYWORD_UNDERLINE = GnColorConstants.GN_KEYWORD
      + EDITOR_UNDERLINE_SUFFIX;

  public static final String EDITOR_VARIABLE_COLOR = GnColorConstants.GN_VARIABLE;
  public final static String EDITOR_VARIABLE_BOLD = GnColorConstants.GN_VARIABLE
      + EDITOR_BOLD_SUFFIX;
  public final static String EDITOR_VARIABLE_ITALIC = GnColorConstants.GN_VARIABLE
      + EDITOR_ITALIC_SUFFIX;
  public final static String EDITOR_VARIABLE_STRIKETHROUGH = GnColorConstants.GN_VARIABLE
      + EDITOR_STRIKETHROUGH_SUFFIX;
  public final static String EDITOR_VARIABLE_UNDERLINE = GnColorConstants.GN_VARIABLE
      + EDITOR_UNDERLINE_SUFFIX;

  public static final String EDITOR_GLOBAL_VARIABLE_COLOR = GnColorConstants.GN_GLOBAL_VARIABLE;
  public final static String EDITOR_GLOBAL_VARIABLE_BOLD = GnColorConstants.GN_GLOBAL_VARIABLE
      + EDITOR_BOLD_SUFFIX;
  public final static String EDITOR_GLOBAL_VARIABLE_ITALIC = GnColorConstants.GN_GLOBAL_VARIABLE
      + EDITOR_ITALIC_SUFFIX;
  public final static String EDITOR_GLOBAL_VARIABLE_STRIKETHROUGH =
      GnColorConstants.GN_GLOBAL_VARIABLE + EDITOR_STRIKETHROUGH_SUFFIX;
  public final static String EDITOR_GLOBAL_VARIABLE_UNDERLINE = GnColorConstants.GN_GLOBAL_VARIABLE
      + EDITOR_UNDERLINE_SUFFIX;

  public static final String FORMATTER_ID = "formatterId"; //$NON-NLS-1$

  public static void initializeDefaultValues(IPreferenceStore store) {
    PreferenceConstants.initializeDefaultValues(store);

    PreferenceConverter.setDefault(store, GnPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR,
        new RGB(63, 127, 95));
    PreferenceConverter.setDefault(store, GnPreferenceConstants.EDITOR_KEYWORD_COLOR, new RGB(127,
        0, 85));
    PreferenceConverter.setDefault(store, GnPreferenceConstants.EDITOR_STRING_COLOR, new RGB(42, 0,
        255));
    PreferenceConverter.setDefault(store, GnColorConstants.GN_DEFAULT, new RGB(0, 0, 0));

    store.setDefault(GnPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD, false);
    store.setDefault(GnPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_ITALIC, false);

    store.setDefault(GnPreferenceConstants.EDITOR_GLOBAL_VARIABLE_ITALIC, true);

    store.setDefault(GnPreferenceConstants.EDITOR_KEYWORD_BOLD, true);
    store.setDefault(GnPreferenceConstants.EDITOR_KEYWORD_ITALIC, false);

    store.setDefault(EDITOR_SMART_INDENT, true);
    store.setDefault(EDITOR_TAB_ALWAYS_INDENT, false);
    store.setDefault(EDITOR_CLOSE_STRINGS, true);
    store.setDefault(EDITOR_CLOSE_BRACKETS, true);
    store.setDefault(EDITOR_CLOSE_BRACES, true);
    store.setDefault(EDITOR_SMART_TAB, true);
    store.setDefault(EDITOR_SMART_PASTE, true);
    store.setDefault(EDITOR_SMART_HOME_END, true);
    store.setDefault(EDITOR_SUB_WORD_NAVIGATION, true);
    store.setDefault(EDITOR_TAB_WIDTH, 2);
    store.setDefault(EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE, true);

    store.setDefault(CodeFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.SPACE);
    store.setDefault(CodeFormatterConstants.FORMATTER_TAB_SIZE, "2"); //$NON-NLS-1$
    store.setDefault(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE, "2"); //$NON-NLS-1$
  }
}
