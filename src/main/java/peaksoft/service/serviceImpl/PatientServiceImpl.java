package peaksoft.service.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import peaksoft.exception.BadRequestException;
import peaksoft.exception.NotFoundException;
import peaksoft.model.Appointment;
import peaksoft.model.Hospital;
import peaksoft.model.Patient;
import peaksoft.repository.AppointmentRepository;
import peaksoft.repository.HospitalRepository;
import peaksoft.repository.PatientRepository;
import peaksoft.service.PatientService;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;

    private final AppointmentRepository appointmentRepository;


    @Override
    public List<Patient> getAllPatient(Long patientId) {
        return patientRepository.getAllPatient(patientId);
    }

    @Override
    public void savePatient(Patient patient, Long hospitalId) {
        Patient patient1 = new Patient();
        if (hospitalRepository.getById(hospitalId) == null){
            throw new NotFoundException(String.format("Hospital with id %d not found",hospitalId));
        }
        Hospital hospital = hospitalRepository.getById(hospitalId);
        patient1.setFirstName(patient.getFirstName());
        patient1.setLastName(patient.getLastName());
        patient1.setEmail(patient.getEmail());
        validation(patient.getPhoneNumber().replace(" ", ""));
        patient1.setGender(patient.getGender());
        patient1.setPhoneNumber(patient.getPhoneNumber());
        patient1.setHospital(hospital);
        hospital.plusPatient();
        patientRepository.save(patient1);
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElseThrow(()-> new BadRequestException("not Patient with id=?"));
    }

    @Override
    public void deletePatientById(Long id) {
        Patient patient = patientRepository.getById(id);
        Hospital hospital = patient.getHospital();
        List<Appointment> appointments = patient.getAppointments();

        appointments.forEach(appointment -> appointment.getPatient().setAppointments(null));

        appointments.forEach(appointment -> appointment.getDoctor().setAppointments(null));

        appointments.forEach(appointment -> appointment.getDepartment().setAppointments(null));

        hospital.getAppointments().removeAll(appointments);
        for (int i = 0; i < appointments.size(); i++) {
            appointmentRepository.deleteById(appointments.get(i).getId());
        }
        patientRepository.delete(patient);
        patient.getHospital().minusPatient();
    }

    @Override
    public void updatePatient(Long patientId, Patient patient) {
        Patient patient1 = patientRepository.getById(patientId);
        patient1.setFirstName(patient.getFirstName());
        patient1.setLastName(patient.getLastName());
        patient1.setEmail(patient.getEmail());
        validation(patient.getPhoneNumber().replace(" ", ""));
        patient1.setPhoneNumber(patient.getPhoneNumber());
        patient1.setGender(patient.getGender());
        patientRepository.save(patient1);
    }

    @Override
    public void assignPatient(Long appointmentId, Long patientId) throws IOException {
        Patient patient = patientRepository.getById(patientId);
        Appointment appointment = appointmentRepository.getById(appointmentId);
        if (appointment.getPatient()!=null){
            for (Patient p: appointment.getHospital().getPatients()) {
                if (p.getId()==patientId){
                    throw new IOException("Bul Patient uje koshulgan");
                }
            }
        }
        patient.addAppointment(appointment);
        appointment.setPatient(patient);
        patientRepository.save(patient);
        appointmentRepository.save(appointment);
    }

    public void validation(String phoneNumber) {
        if (phoneNumber.length() == 13
                && phoneNumber.charAt(0) == '+'
                && phoneNumber.charAt(1) == '9'
                && phoneNumber.charAt(2) == '9'
                && phoneNumber.charAt(3) == '6'){
            int count = 0;
            for (Character i : phoneNumber.toCharArray()) {
                if (count != 0){
                    if (!Character.isDigit(i)){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"номер!!!");
                    }
                }
                count++;
            }
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"номер!!!");
        }
    }
}
