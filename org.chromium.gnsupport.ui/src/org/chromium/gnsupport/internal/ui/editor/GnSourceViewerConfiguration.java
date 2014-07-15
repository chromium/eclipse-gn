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

import org.chromium.gnsupport.internal.ui.text.completion.GnCompletionProcessor;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;

public class GnSourceViewerConfiguration extends ScriptSourceViewerConfiguration {

  private AbstractScriptScanner codeScanner;
  private AbstractScriptScanner stringScanner;
  private AbstractScriptScanner commentScanner;

  public GnSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
      ITextEditor editor, String partitioning) {
    super(colorManager, preferenceStore, editor, partitioning);
  }

  @Override
  public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
    return new IAutoEditStrategy[] {new DefaultIndentLineAutoEditStrategy()};
  }

  @Override
  protected ContentAssistPreference getContentAssistPreference() {
    return GnContentAssistPreference.getDefault();
  }

  @Override
  protected void initializeScanners() {
    IPreferenceStore preferenceStore = this.getPreferenceStore();
    codeScanner = new GnCodeScanner(this.getColorManager(), preferenceStore);
    stringScanner = new SingleTokenScriptScanner(
        this.getColorManager(), preferenceStore, GnColorConstants.GN_STRING);
    commentScanner = createCommentScanner(GnColorConstants.GN_COMMENT, GnColorConstants.GN_COMMENT);
  }

  @Override
  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
    PresentationReconciler reconciler = new ScriptPresentationReconciler();
    reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(this.codeScanner);
    reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
    reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

    dr = new DefaultDamagerRepairer(this.stringScanner);
    reconciler.setDamager(dr, GnPartitions.GN_STRING);
    reconciler.setRepairer(dr, GnPartitions.GN_STRING);

    dr = new DefaultDamagerRepairer(this.commentScanner);
    reconciler.setDamager(dr, GnPartitions.GN_COMMENT);
    reconciler.setRepairer(dr, GnPartitions.GN_COMMENT);

    return reconciler;
  }

  @Override
  public void handlePropertyChangeEvent(PropertyChangeEvent event) {
    if (this.codeScanner.affectsBehavior(event)) {
      this.codeScanner.adaptToPreferenceChange(event);
    }

    if (this.stringScanner.affectsBehavior(event)) {
      this.stringScanner.adaptToPreferenceChange(event);
    }

    if (this.commentScanner.affectsBehavior(event)) {
      this.commentScanner.adaptToPreferenceChange(event);
    }
  }

  @Override
  public boolean affectsTextPresentation(PropertyChangeEvent event) {
    return this.codeScanner.affectsBehavior(event)
        || this.stringScanner.affectsBehavior(event)
        || this.commentScanner.affectsBehavior(event);
  }

  @Override
  protected void alterContentAssistant(ContentAssistant assistant) {
    IContentAssistProcessor scriptProcessor =
        new GnCompletionProcessor(getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
    assistant.setContentAssistProcessor(scriptProcessor, IDocument.DEFAULT_CONTENT_TYPE);
  }
}
