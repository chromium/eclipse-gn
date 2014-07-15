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

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;

public class GnMethodDeclaration extends MethodDeclaration {
  private Type type;
  private SimpleReference originalRef;

  public static enum Type {
    NORMAL, TEMPLATE, TARGET, TEMPLATE_CALL
  }

  public GnMethodDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd) {
    super(name, nameStart, nameEnd, declStart, declEnd);
    type = Type.NORMAL;
    SimpleReference ref = super.getRef();
    originalRef = new SimpleReference(ref.start(), ref.end(), ref.getName());
  }

  public SimpleReference getOriginalRef() {
    return originalRef;
  }

  public ASTNode getFirstArgument() {
    if (getArguments().isEmpty()) {
      return null;
    }
    return ((GnArgument) getArguments().get(0)).getActualNode();
  }

  @Override
  public String getName() {
    return getRef().toString();
  }


  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
    ASTNode newRef;
    switch (type) {
      case TEMPLATE:
      case TARGET:
        ASTNode arg = getFirstArgument();
        newRef = arg == null ? originalRef : arg;
        break;
      case NORMAL:
      case TEMPLATE_CALL:
      default:
        newRef = originalRef;
        break;
    }
    this.setName(newRef.toString());
    this.setNameStart(newRef.start());
    this.setNameEnd(newRef.end());
  }

}
