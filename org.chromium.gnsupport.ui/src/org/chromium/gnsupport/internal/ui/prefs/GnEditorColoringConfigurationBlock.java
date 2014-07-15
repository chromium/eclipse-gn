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
package org.chromium.gnsupport.internal.ui.prefs;

import org.chromium.gnsupport.internal.ui.GnPreferenceConstants;
import org.chromium.gnsupport.internal.ui.editor.GnDocumentSetupParticipant;
import org.chromium.gnsupport.internal.ui.editor.GnPartitions;
import org.chromium.gnsupport.internal.ui.editor.SimpleGnSourceViewerConfiguration;
import org.chromium.gnsupport.internal.ui.text.GnSemanticUpdateWorker;
import org.chromium.gnsupport.ui.GnUiPlugin;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.preferences.AbstractScriptEditorColoringConfigurationBlock;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditor;

import java.io.InputStream;

public class GnEditorColoringConfigurationBlock extends
    AbstractScriptEditorColoringConfigurationBlock implements IPreferenceConfigurationBlock {

  private static final String PREVIEW_FILE_NAME = "PreviewFile.gn"; //$NON-NLS-1$

  private static final String[][] SYNTAX_COLOR_LIST_MODEL = new String[][] {
      {
        PreferencesMessages.DLTKEditorPreferencePage_singleLineComment,
        GnPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR, sCommentsCategory
      },
      {
        PreferencesMessages.DLTKEditorPreferencePage_keywords,
        GnPreferenceConstants.EDITOR_KEYWORD_COLOR, sCoreCategory
      },
      {
        PreferencesMessages.DLTKEditorPreferencePage_strings,
        GnPreferenceConstants.EDITOR_STRING_COLOR, sCoreCategory
      },
      {
        PreferencesMessages.DLTKEditorPreferencePage_numbers,
        GnPreferenceConstants.EDITOR_NUMBER_COLOR, sCoreCategory
      }
  };

  public GnEditorColoringConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
    super(overlayPreferenceStore);
  }

  @Override
  protected String[][] getSyntaxColorListModel() {
    return SYNTAX_COLOR_LIST_MODEL;
  }

  @Override
  protected ProjectionViewer createPreviewViewer(Composite parent, IVerticalRuler verticalRuler,
      IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles,
      IPreferenceStore store) {
    return new ScriptSourceViewer(
        parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles, store);
  }

  @Override
  protected ScriptSourceViewerConfiguration createSimpleSourceViewerConfiguration(
      IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor,
      boolean configureFormatter) {
    return new SimpleGnSourceViewerConfiguration(colorManager, preferenceStore, editor,
        GnPartitions.GN_PARTITIONING, configureFormatter);
  }

  @Override
  protected void setDocumentPartitioning(IDocument document) {
    GnDocumentSetupParticipant participant = new GnDocumentSetupParticipant();
    participant.setup(document);
  }

  @Override
  protected InputStream getPreviewContentReader() {
    return getClass().getResourceAsStream(PREVIEW_FILE_NAME);
  }

  @Override
  protected ScriptTextTools getTextTools() {
    return GnUiPlugin.getDefault().getTextTools();
  }

  @Override
  protected SemanticHighlighting[] getSemanticHighlightings() {
    return new GnSemanticUpdateWorker().getSemanticHighlightings();
  }
}
