package src.core.utils;

public interface DatasetReader
{
    public Point nextPoint();
    public void close();
}