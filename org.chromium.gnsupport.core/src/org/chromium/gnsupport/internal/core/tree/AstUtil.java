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
package org.chromium.gnsupport.internal.core.tree;

import org.chromium.gnsupport.core.GnCorePlugin;
import org.chromium.gnsupport.core.ast.GnFieldDeclaration;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.statements.Block;

public class AstUtil {

  public static ASTNode findMinimalNode(ModuleDeclaration unit, int startOffset, int endOffset) {
    Visitor visitor = new Visitor(startOffset, endOffset);

    try {
      unit.traverse(visitor);
    } catch (Exception e) {
      GnCorePlugin.log(e);
    }

    return visitor.getResult();
  }

  private static class Visitor extends ASTVisitor {
    private ASTNode result;
    private final int start;
    private final int end;

    public Visitor(int start, int end) {
      this.start = start;
      this.end = end;
    }

    public ASTNode getResult() {
      return result;
    }

    @Override
    public boolean visitGeneral(ASTNode node) throws Exception {
      int realStart = node.sourceStart();
      int realEnd = node.sourceEnd();
      if (node instanceof Block) {
        return true;
      }
      if (realStart <= start && realEnd >= end) {
        if (result == null || isBetterMatch(node)) {
          result = node;
        }
      }
      return !(result instanceof GnFieldDeclaration);
    }

    private boolean isBetterMatch(ASTNode s) {
      return result.sourceStart() <= s.sourceStart() && s.sourceEnd() <= result.sourceEnd();
    }
  }

  private AstUtil() {
    // Non-instantiable.
  }
}
