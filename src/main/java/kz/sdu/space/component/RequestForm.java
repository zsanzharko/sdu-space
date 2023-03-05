package kz.sdu.space.component;

public interface RequestForm<D extends DataTransfer> {
  D getDataTransfer();
}
