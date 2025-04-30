package concurrencia;

public interface ReadWriteController {
    public abstract void request_read() throws InterruptedException;
    public abstract void release_read() throws InterruptedException;
    public abstract void request_write() throws InterruptedException;
    public abstract void release_write() throws InterruptedException;
}
