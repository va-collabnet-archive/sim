/*
 * United States Government Work
 *
 * Veterans Health Administration
 * US Department of Veterans Affairs
 *
 * US Federal Government Agencies are required
 * to release their works as Public Domain.
 *
 * http://www.copyright.gov/title17/92chap1.html#105
 */

package gov.va.sim.measurement;


/**
 *
 * @author kec
 */
public interface PointBI extends MeasurementBI<PointBI> {
    Number getPointValue();
    void setPointValue(Number pointValue);
}
