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
package org.chromium.gnsupport.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("nls")
public class GnCodeUtil {
  public static final Set<String> TARGET_NAMES = new HashSet<String>(
      Arrays.asList("action", "action_foreach", "config", "copy", "idl", "static_library",
          "source_set", "shared_library", "component", "executable", "test", "group"));

  public static final Set<String> EXTERN_VARIABLES = new HashSet<String>(
      Arrays.asList("is_asan", "is_chrome_branded", "is_official_build", "is_clang",
          "is_component_build", "is_desktop_linux", "symbol_level", "is_lsan", "is_msan",
          "cpu_arch", "is_tsan", "is_debug", "is_win", "is_linux", "is_android", "is_mac",
          "is_ios", "is_chromeos", "root_build_dir"));

  public static final String[] KEYWORDS;

  static {
    List<String> languageKeywords =
        Arrays.asList("true", "false", "if", "else", "print", "assert", "import", "invoker",
            "set_defaults", "source_set", "template", "tool", "toolchain", "toolchain_args");
    List<String> keywords = new ArrayList<String>(languageKeywords.size() + TARGET_NAMES.size());
    keywords.addAll(languageKeywords);
    keywords.addAll(TARGET_NAMES);

    KEYWORDS = keywords.toArray(new String[keywords.size()]);
  }

  public static boolean isExternVariable(String name) {
    return EXTERN_VARIABLES.contains(name);
  }

  public static boolean isTarget(String functionName) {
    return TARGET_NAMES.contains(functionName);
  }

  public static boolean isTemplate(String functionName) {
    return "template".equals(functionName); //$NON-NLS-1$
  }

  public static boolean isImport(String functionName) {
    return "import".equals(functionName); //$NON-NLS-1$
  }

  private GnCodeUtil() {
    // Non-instantiable.
  }
}
