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
package org.chromium.gnsupport.internal.core.parser;

import static org.junit.Assert.assertEquals;

import org.chromium.gnsupport.core.ast.GnSymbolFactory;
import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

import java.io.StringReader;

public class AbstractParserTest {
  protected static class TestProblemReporter implements IProblemReporter {
    private int errorCount = 0;
    private IProblem lastProblem;

    @Override
    public void reportProblem(IProblem problem) {
      ++errorCount;
      lastProblem = problem;
    }

    public int getErrorCount() {
      return errorCount;
    }

    public String getLastMessage() {
      return lastProblem == null ? null : lastProblem.getMessage();
    }

    public int getLastErrorStart() {
      return lastProblem == null ? -100 : lastProblem.getSourceStart();
    }
  }

  protected static ASTListNode parse(String text, IProblemReporter reporter) throws Exception {
    GnParser parser =
        new GnParser(new StringReader(text), new GnSymbolFactory(), "BUILD.gn", reporter); //$NON-NLS-1$
    return (ASTListNode) parser.parse().value;
  }

  protected static ASTListNode parseClean(String text) throws Exception {
    TestProblemReporter reporter = new TestProblemReporter();
    ASTListNode result = parse(text, reporter);
    assertEquals(0, reporter.getErrorCount());
    return result;
  }
}
