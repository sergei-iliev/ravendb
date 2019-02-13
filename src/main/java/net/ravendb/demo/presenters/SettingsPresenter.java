package net.ravendb.demo.presenters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.flow.component.upload.SucceededEvent;

import net.ravendb.demo.model.Doctor;
import net.ravendb.demo.model.Patient;
import net.ravendb.demo.model.Patient.Gender;
import net.ravendb.demo.presenters.DoctorViewable.DoctorViewListener;
import net.ravendb.demo.presenters.PatientViewable.PatientViewListener;
import net.ravendb.demo.presenters.SettingsViewable.SettingsViewListener;

public class SettingsPresenter implements SettingsViewListener{
   private final SettingsViewable view;
   
   public SettingsPresenter(SettingsViewable view) {
	   this.view=view;
   }


   
}
