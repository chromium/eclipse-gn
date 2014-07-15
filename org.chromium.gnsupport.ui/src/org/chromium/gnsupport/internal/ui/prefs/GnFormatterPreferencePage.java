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

import org.chromium.gnsupport.core.GnNature;
import org.chromium.gnsupport.internal.ui.GnPreferenceConstants;
import org.chromium.gnsupport.internal.ui.editor.GnPartitions;
import org.chromium.gnsupport.internal.ui.editor.SimpleGnSourceViewerConfiguration;
import org.chromium.gnsupport.ui.GnUiPlugin;
import org.eclipse.dltk.ui.formatter.AbstractFormatterPreferencePage;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

public class GnFormatterPreferencePage extends AbstractFormatterPreferencePage {

  private static final PreferenceKey FORMATTER = new PreferenceKey(
      GnUiPlugin.PLUGIN_ID, GnPreferenceConstants.FORMATTER_ID);

  @Override
  protected String getNatureId() {
    return GnNature.ID;
  }

  @Override
  protected PreferenceKey getFormatterPreferenceKey() {
    return FORMATTER;
  }

  @Override
  protected IDialogSettings getDialogSettings() {
    return GnUiPlugin.getDefault().getDialogSettings();
  }

  @Override
  protected String getPreferencePageId() {
    return "org.chromium.gnsupport.preferences.formatter"; //$NON-NLS-1$
  }

  @Override
  protected String getPropertyPageId() {
    return null;
  }

  @Override
  protected ScriptSourceViewerConfiguration createSimpleSourceViewerConfiguration(
      IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor,
      boolean configureFormatter) {
    return new SimpleGnSourceViewerConfiguration(colorManager, preferenceStore, editor,
        GnPartitions.GN_PARTITIONING, configureFormatter);
  }

  @Override
  protected void setPreferenceStore() {
    setPreferenceStore(GnUiPlugin.getDefault().getPreferenceStore());
  }
}
