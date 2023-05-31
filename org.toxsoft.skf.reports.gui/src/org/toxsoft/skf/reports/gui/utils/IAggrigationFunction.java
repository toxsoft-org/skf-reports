package org.toxsoft.skf.reports.gui.utils;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;

/**
 * Функция аггрегации поступающих значений
 *
 * @author max
 */
public interface IAggrigationFunction {

  /**
   * Обаботка следующего значения
   *
   * @param aValue IAtomicValue - обрабатываемое значение
   */
  void nextValue( IAtomicValue aValue );

  /**
   * Возвращает текущей результат обработки
   *
   * @return IAtomicValue - текущей результат обработки
   */
  IAtomicValue getCurrentResult();

  /**
   * Пустая функция Аггрегации
   */
  IAggrigationFunction EMPTY_AFFR_FUNC = new EmptyAggrFunction();

  /**
   * Пустая функция агрегации.
   *
   * @author max
   */
  class EmptyAggrFunction
      implements IAggrigationFunction {

    @Override
    public void nextValue( IAtomicValue aValue ) {
      // non
    }

    @Override
    public IAtomicValue getCurrentResult() {
      return IAtomicValue.NULL;
    }

  }

  /**
   * Абстрактная реализация функуи аггрегации
   *
   * @author max
   */
  abstract class AbstractAggrFunction
      implements IAggrigationFunction {

    private int valueCount = 0;

    protected IAtomicValue result = IAtomicValue.NULL;

    @Override
    public IAtomicValue getCurrentResult() {
      return result;
    }

    @Override
    public void nextValue( IAtomicValue aValue ) {
      if( aValue == null || !aValue.isAssigned() ) {
        return;
      }
      if( valueCount == 0 ) {
        doFirstValue( aValue );
      }
      else {
        doNextValue( aValue, valueCount );
      }

      valueCount++;

    }

    abstract protected void doNextValue( IAtomicValue aValue, int aCurrCount );

    protected void doFirstValue( IAtomicValue aValue ) {
      result = aValue;
    }

  }

  /**
   * Реализация функции среднего
   *
   * @author max
   */
  class AverageAggrFunction
      extends AbstractAggrFunction {

    @Override
    protected void doNextValue( IAtomicValue aValue, int aCurrCount ) {
      double currRes = result.asDouble();
      double newVal = aValue.asDouble();

      double newRes = (currRes * aCurrCount + newVal) / (aCurrCount + 1);
      result = AvUtils.avFloat( newRes );
    }
  }

  /**
   * Реализация функции мин (макс)
   *
   * @author max
   */
  class RangeFunction
      extends AbstractAggrFunction {

    private boolean isMin = true;

    public RangeFunction( boolean aIsMin ) {
      super();
      isMin = aIsMin;
    }

    @Override
    protected void doNextValue( IAtomicValue aValue, int aCurrCount ) {
      if( isMin ) {
        result = aValue.asDouble() < result.asDouble() ? aValue : result;
      }
      else {
        result = aValue.asDouble() > result.asDouble() ? aValue : result;
      }
    }
  }

  /**
   * Реализация функции суммы
   *
   * @author max
   */
  class SumAggrFunction
      extends AbstractAggrFunction {

    @Override
    protected void doNextValue( IAtomicValue aValue, int aCurrCount ) {
      if( aValue.atomicType() == EAtomicType.INTEGER ) {
        result = AvUtils.avInt( result.asInt() + aValue.asInt() );
      }
      else {
        result = AvUtils.avFloat( result.asDouble() + aValue.asDouble() );
      }
    }
  }
}
