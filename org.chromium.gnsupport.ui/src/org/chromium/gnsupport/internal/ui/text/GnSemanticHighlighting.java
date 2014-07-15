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
package org.chromium.gnsupport.internal.ui.text;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;

public class GnSemanticHighlighting extends SemanticHighlighting {
  private final String preferenceKey;
  private final String displayName;
  private final boolean enablement;

  public GnSemanticHighlighting(String preferenceKey, String displayName) {
    this(preferenceKey, displayName, true);
  }

  public GnSemanticHighlighting(String preferenceKey, String displayName, boolean enablement) {
    Assert.isNotNull(preferenceKey);
    this.preferenceKey = preferenceKey;
    this.displayName = displayName;
    this.enablement = enablement;
  }

  @Override
  public String getPreferenceKey() {
    return preferenceKey;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public boolean isSemanticOnly() {
    return displayName != null;
  }

  @Override
  public String getEnabledPreferenceKey() {
    return enablement ? super.getEnabledPreferenceKey() : null;
  }

  @Override
  public int hashCode() {
    return preferenceKey.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof GnSemanticHighlighting) {
      GnSemanticHighlighting other = (GnSemanticHighlighting) obj;
      return preferenceKey.equals(other.preferenceKey);
    }
    return false;
  }
}
