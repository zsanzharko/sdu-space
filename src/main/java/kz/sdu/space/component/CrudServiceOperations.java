package kz.sdu.space.component;

import java.util.List;

public interface CrudServiceOperations<D extends DataTransfer, F extends RequestForm<?>> {
  D create(F f);
  D read(Long id);
  List<D> readAll();
  void update(D d);
  void delete(Long id);
}
