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
package org.chromium.gnsupport.core.ast;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;

import org.eclipse.dltk.ast.ASTNode;

public class GnSymbolFactory extends ComplexSymbolFactory {

  @Override
  public Symbol newSymbol(String name, int id, Symbol left, Symbol right, Object value) {
    ComplexSymbol sym = (ComplexSymbol) super.newSymbol(name, id, left, right, value);
    if (value instanceof ASTNode) {
      ASTNode node = (ASTNode) value;
      node.setStart(left.left);
      node.setEnd(right.right);
    }
    return sym;
  }

  @Override
  public Symbol newSymbol(String name, int id, Location left, Location right, Object value) {
    ComplexSymbol sym = (ComplexSymbol) super.newSymbol(name, id, left, right, value);
    if (value instanceof ASTNode) {
      ASTNode node = (ASTNode) value;
      node.setStart(left.getOffset());
      node.setEnd(right.getOffset());
    }
    return sym;
  }
}
