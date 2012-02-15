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
 * Interface for an upper and lower bound of a numeric value. 
 * 
 * @author kec
 */
public interface BoundBI extends MeasurementBI<BoundBI> {
    /**
     * 
     * @return upper limit of this bound
     */
    PointBI getUpperLimit();
    
    /**
     * 
     * @return lower limit of this bound
     */
    PointBI getLowerLimit();
    
    /**
     * 
     * @param upperLimit the upper limit of this bound
     */
    void setUpperLimit(PointBI upperLimit);
    
    /**
     * 
     * @param lowerLimit the lower limit of this bound
     */
    void setLowerLimit(PointBI lowerLimit);
}
