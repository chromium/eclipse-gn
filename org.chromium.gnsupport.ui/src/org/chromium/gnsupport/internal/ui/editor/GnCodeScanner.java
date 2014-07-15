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
package org.chromium.gnsupport.internal.ui.editor;

import org.chromium.gnsupport.core.GnCodeUtil;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import java.util.ArrayList;
import java.util.List;

public class GnCodeScanner extends AbstractScriptScanner {

  private static final String TOKEN_PROPERTIES[] = new String[] {
    GnColorConstants.GN_COMMENT,
    GnColorConstants.GN_DEFAULT,
    GnColorConstants.GN_KEYWORD,
    GnColorConstants.GN_NUMBER
  };

  public static String[] findKeywordsByPrefix(String prefix) {
    List<String> result = new ArrayList<String>(4);
    for (String keyword : GnCodeUtil.KEYWORDS) {
      if (keyword.startsWith(prefix)) {
        result.add(keyword);
      }
    }
    return result.toArray(new String[result.size()]);
  }

  public GnCodeScanner(IColorManager manager, IPreferenceStore store) {
    super(manager, store);
    this.initialize();
  }

  @Override
  protected String[] getTokenProperties() {
    return TOKEN_PROPERTIES;
  }

  @Override
  protected List<IRule> createRules() {
    List<IRule> rules = new ArrayList<IRule>();
    IToken keyword = this.getToken(GnColorConstants.GN_KEYWORD);
    IToken numberToken = getToken(GnColorConstants.GN_NUMBER);
    IToken comment = this.getToken(GnColorConstants.GN_COMMENT);
    IToken other = this.getToken(GnColorConstants.GN_DEFAULT);

    rules.add(new EndOfLineRule("#", comment));
    rules.add(new WhitespaceRule(new GnWhitespaceDetector()));

    WordRule wordRule = new WordRule(new GnWordDetector(), other);
    for (int i = 0; i < GnCodeUtil.KEYWORDS.length; i++) {
      wordRule.addWord(GnCodeUtil.KEYWORDS[i], keyword);
    }
    rules.add(wordRule);

    NumberRule numberRule = new NumberRule(numberToken);
    rules.add(numberRule);

    this.setDefaultReturnToken(other);
    return rules;
  }

  private static class GnWhitespaceDetector implements IWhitespaceDetector {

    @Override
    public boolean isWhitespace(char character) {
      return Character.isWhitespace(character);
    }
  }

  private static class GnWordDetector implements IWordDetector {

    @Override
    public boolean isWordPart(char character) {
      return Character.isJavaIdentifierPart(character);
    }

    @Override
    public boolean isWordStart(char character) {
      return Character.isJavaIdentifierStart(character);
    }
  }
}
