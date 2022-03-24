import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class medicalServiceTests {

    // Сделал один тестовый метод, но параметризованный. Он проверяет все возможные варианты когда сообщение должно
    // выводиться, а когда не должно. Если это неверно могу переделать на 4 обычных теста при доработке.
    @ParameterizedTest
    @ValueSource(strings = {"badTemperature", "badPressure", "normalTemperature", "normalPressure"})
    void messageNotDisplayedTest(String indicator) {

        BigDecimal badTemperature = new BigDecimal("34.1");
        BloodPressure badPressure = new BloodPressure(110, 80);
        BigDecimal normalTemperature = new BigDecimal("36.6");
        BloodPressure normalPressure = new BloodPressure(120, 80);

        String patientID = "0";
        SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);

        Mockito.when(patientInfoRepository.getById(patientID)).thenReturn(
                new PatientInfo(patientID, "Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))));

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        switch (indicator) {
            case "badTemperature":  // Сообщение должно быть вызвано. Низкая температура

                medicalService.checkTemperature(patientID, badTemperature);
                Mockito.verify(sendAlertService, Mockito.only()).send(String.format("Warning, patient with id: %s, need help",
                        patientID));

                break;
            case "badPressure":  // Сообщение должно быть вызвано. Низкое давление

                medicalService.checkBloodPressure(patientID, badPressure);
                Mockito.verify(sendAlertService, Mockito.only()).send(String.format("Warning, patient with id: %s, need help",
                        patientID));

                break;
            case "normalTemperature":  // Сообщение НЕ должно быть вызвано. Нормальная температура

                medicalService.checkTemperature(patientID, normalTemperature);
                Mockito.verify(sendAlertService, Mockito.never()).send(String.format("Warning, patient with id: %s, need help",
                        patientID));

                break;
            case "normalPressure":  // Сообщение НЕ должно быть вызвано. Нормальное давление

                medicalService.checkBloodPressure(patientID, normalPressure);
                Mockito.verify(sendAlertService, Mockito.never()).send(String.format("Warning, patient with id: %s, need help",
                        patientID));

                break;
        }

    }

}
