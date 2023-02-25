package kz.sdu.space.component;

public interface Converter<D extends DataTransfer, E> {
  D convertEntity(E e);
  E convertDto(D d);
}
