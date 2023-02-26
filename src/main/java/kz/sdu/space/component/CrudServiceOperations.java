package kz.sdu.space.component;

import kz.sdu.space.exception.IdNotFoundException;
import kz.sdu.space.exception.InvalidInputException;

public interface CrudServiceOperations<D extends DataTransfer> {
  D create(D d) throws InvalidInputException;
  D read(Long id) throws IdNotFoundException;
  void update(D d) throws InvalidInputException;
  void delete(Long id);
}
