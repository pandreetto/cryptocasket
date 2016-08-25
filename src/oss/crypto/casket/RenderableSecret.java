package oss.crypto.casket;

public interface RenderableSecret
    extends Secret {

    public String getValue();

    public int getLayoutId();

    public int getKeyResourceId();

    public int getValueResourceId();

}