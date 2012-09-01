/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ohi.sim.fx.description.view.table;

import gov.va.ohi.sim.fx.component.view.table.AbstractComponentCellValueFactory;
import org.ihtsdo.fxmodel.concept.component.FxComponentChronicle;
import org.ihtsdo.fxmodel.concept.component.description.FxDescriptionVersion;

/**
 *
 * @author kec
 */
public class DescriptionLanguageCellValueFactory 
        extends AbstractComponentCellValueFactory<FxDescriptionVersion,
           FxComponentChronicle<FxDescriptionVersion, ?>> {
   @Override
   public String getStringValue(FxDescriptionVersion version) {
      return version.getLanguage();
   }
}