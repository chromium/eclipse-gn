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

import org.chromium.gnsupport.core.GnLanguageToolkit;
import org.chromium.gnsupport.ui.GnUiPlugin;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.ui.IEditorInput;

public class GnEditor extends ScriptEditor {
  public static final String EDITOR_ID = "org.chromium.gnsupport.ui.editor"; //$NON-NLS-1$
  public static final String EDITOR_CONTEXT = "#GNEditorContext"; //$NON-NLS-1$

  @Override
  protected void initializeEditor() {
    super.initializeEditor();
    setEditorContextMenuId(EDITOR_CONTEXT);
  }

  @Override
  public String getEditorId() {
    return EDITOR_ID;
  }

  @Override
  public IPreferenceStore getScriptPreferenceStore() {
    return GnUiPlugin.getDefault().getPreferenceStore();
  }

  @Override
  public IDLTKLanguageToolkit getLanguageToolkit() {
    return GnLanguageToolkit.getDefault();
  }

  @Override
  public ScriptTextTools getTextTools() {
    return GnUiPlugin.getDefault().getTextTools();
  }

  @Override
  protected void initializeKeyBindingScopes() {
    setKeyBindingScopes(new String[] {"org.chromium.gnsupport.gnEditorScope"}); //$NON-NLS-1$
  }

  @Override
  protected ICharacterPairMatcher createBracketMatcher() {
    return new GnPairMatcher();
  }

  @Override
  protected void connectPartitioningToElement(IEditorInput input, IDocument document) {
    if (document instanceof IDocumentExtension3) {
      IDocumentExtension3 extension = (IDocumentExtension3) document;
      if (extension.getDocumentPartitioner(GnPartitions.GN_PARTITIONING) == null) {
        GnTextTools tools = GnUiPlugin.getDefault().getTextTools();
        tools.setupDocumentPartitioner(document, GnPartitions.GN_PARTITIONING);
      }
    }
  }

  private static class GnPairMatcher extends DefaultCharacterPairMatcher {
    public GnPairMatcher() {
      super("{}[]()".toCharArray(), GnPartitions.GN_PARTITIONING); //$NON-NLS-1$
    }
  }
}
