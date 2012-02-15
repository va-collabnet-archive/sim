
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
package gov.va.sim.act;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.sim.act.expression.ExpressionBI;

import org.ihtsdo.tk.api.concept.ConceptVersionBI;

/**
 * Assertion: the act of stating something
 *
 * @author Keith E. Campbell
 */
public interface AssertionBI extends ActBI {

   /**
    * Discernible: perceptible by the senses or intellect.
    *
    * Negation within a discernable are not allowed. Conditions
    * support the negation of an entire discernable.
    */
   ExpressionBI getDiscernable();

   /**
    * A representation of the the
    * strength of an asserter's belief that the value
    * is correct.  Probabilities are represented as integers inclusively
    * between 0 and 100. Probability can be represented as a point, a bound,
    * or an interval.
    */
   int[] getProbability();

   /**
    * A ConceptVersionBI that
    * qualifies the value, making it less general. For example,
    * a qualifier of "unknown, not asked" applied to a value of
    * "null" makes the meaning of "null" more specific (less general).
    * This qualification allows representation of the HL7 null flavors,
    * in addition to allowing a means to qualify substantive values
    * with qualifiers such as "default", "patient entered", and
    * "confirmed default".
    */
   ConceptVersionBI getQualifier();

   /**
    * A representation of the asserter's belief in the timing during
    * which this assertion is valid. If the interval is null,
    * then the interval of the EventDocBI is assumed.
    * Timing can be represented as a point, a bound, or an
    * interval.
    */
   long[] getTiming();

   /**
    * The value of a condition can be of several types:
    *
    * numeric: point, bound, or interval.
    *         e.g a patients pulse might be
    *          [65] or [60,70], or [59,60,69,70]
    *
    * Encoded expression.
    *
    * boolean: true, false
    *
    * The value must only indicate the presence, absence, or scalar magnitude of an assertion. It must not
    * modify the discernable with qualifiers such as severity, onset, quality, etc. The value of the assertion
    * must never be a value statement, or a conclusion, about the discernable. For example A value of "high"
    * for a discernable of "blood pressure" is not allowed. But a value of "present" for a discernable of
    * "high blood pressure" is allowed.
    *
    * @return
    */
   ExpressionBI getValue();
}
