/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.ap.testmodel.annotationparameters;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * @author Marko Bekhta
 */
public class InvalidDecimalMinMaxParameters {

	@DecimalMax( value = "a" )
	@DecimalMin( value = "a" )
	private BigDecimal decimal1;

	@DecimalMax( value = "123.12.00" )
	@DecimalMin( value = "123.12.00" )
	private BigDecimal decimal2;

	@DecimalMax( value = "123E-3-3" )
	@DecimalMin( value = "123E-3-3" )
	private BigDecimal decimal3;

	@DecimalMax.List({ @DecimalMax(value = "123E-3-3") })
	@DecimalMin.List({ @DecimalMin(value = "123E-3-3") })
	private BigDecimal decimal4;

}