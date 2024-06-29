package com.koyomiji.legacy_remapper;

public class SeargeExceptorGlobal {
  private int constructorIndex;
  private ParamNameStyle paramNameStyle;
  private LocalVariableNameStyle localVariableNameStyle;

  public SeargeExceptorGlobal() {
    this(1, ParamNameStyle.FUNC_ID_INDEX, LocalVariableNameStyle.LVT_INDEX_SUB);
  }

  public SeargeExceptorGlobal(int constructorIndex,
                              ParamNameStyle paramNameStyle,
                              LocalVariableNameStyle localVariableNameStyle) {
    this.constructorIndex = constructorIndex;
    this.paramNameStyle = paramNameStyle;
    this.localVariableNameStyle = localVariableNameStyle;
  }

  public void incrementConstructorIndex() { constructorIndex++; }

  public int getConstructorIndex() { return constructorIndex; }

  public void setConstructorIndex(int constructorIndex) {
    this.constructorIndex = constructorIndex;
  }

  public ParamNameStyle getParamNameStyle() { return paramNameStyle; }

  public void setParamNameStyle(ParamNameStyle paramNameStyle) {
    this.paramNameStyle = paramNameStyle;
  }

  public LocalVariableNameStyle getLocalVariableNameStyle() {
    return localVariableNameStyle;
  }

  public void
  setLocalVariableNameStyle(LocalVariableNameStyle localVariableNameStyle) {
    this.localVariableNameStyle = localVariableNameStyle;
  }
}
