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
package org.chromium.gnsupport.internal.ui.text.completion;

import org.chromium.gnsupport.core.GnNature;
import org.eclipse.dltk.codeassist.RelevanceConstants;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.swt.graphics.Image;

public class GnCompletionProposalCollector extends ScriptCompletionProposalCollector {

  private static final char[] GN_VAR_TRIGGER = {'\t', ' ', '=', ';', '.'};

  @Override
  protected char[] getVarTrigger() {
    return GN_VAR_TRIGGER;
  }

  public GnCompletionProposalCollector(ISourceModule sourceModule) {
    super(sourceModule);
  }

  @Deprecated
  @Override
  protected ScriptCompletionProposal createScriptCompletionProposal(String completion,
      int replaceStart, int length, Image image, String displayString, int i) {
    return new GnCompletionProposal(completion, replaceStart, length, image, displayString, i);
  }

  @Override
  protected ScriptCompletionProposal createScriptCompletionProposal(String completion,
      int replaceStart, int length, Image image, String displayString, int i, boolean isInDoc) {
    return new GnCompletionProposal(completion, replaceStart, length, image, displayString, i,
        isInDoc);
  }

  @Override
  protected ScriptCompletionProposal createOverrideCompletionProposal(IScriptProject scriptProject,
      ISourceModule compilationUnit, String name, String[] paramTypes, int start, int length,
      String label, String string) {
    String replacementString = name + "()";
    GnCompletionProposal proposal =
        new GnCompletionProposal(replacementString, start, length, null, label,
            RelevanceConstants.R_INTERESTING);
    proposal.setCursorPosition(replacementString.length() - 1);
    return proposal;
  }

  @Override
  protected String getNatureId() {
    return GnNature.ID;
  }
}
