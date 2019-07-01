package pl.coderslab.javaGym.service.dataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.javaGym.dataTransferObject.EmailDto;
import pl.coderslab.javaGym.emailSender.EmailSender;
import pl.coderslab.javaGym.entity.data.Instructor;
import pl.coderslab.javaGym.error.customException.ActionNotAllowedException;
import pl.coderslab.javaGym.error.customException.UniqueDBFieldException;
import pl.coderslab.javaGym.error.customException.EmailSendingException;
import pl.coderslab.javaGym.error.customException.ResourceNotFoundException;
import pl.coderslab.javaGym.repository.InstructorRepository;

import java.util.List;

@Service
public class InstructorService implements AbstractDataService<Instructor> {

    private InstructorRepository instructorRepository;
    private EmailSender emailSender;

    @Autowired
    public InstructorService(InstructorRepository instructorRepository, EmailSender emailSender) {
        this.instructorRepository = instructorRepository;
        this.emailSender = emailSender;
    }

    @Override
    public List<Instructor> findAll() {
        return null;
    }

    @Override
    public Instructor findById(Long id) {
        return getInstructorById(id);
    }

    private Instructor getInstructorById(Long id) {
        Instructor instructor = instructorRepository.findById(id).orElse(null);
        if (instructor != null) {
            return instructor;
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    @Transactional
    public Instructor save(Instructor instructor) {
        if (instructor.getId() == null) {
            if (!isInstructorEmailAlreadyInDB(instructor)) {
                return instructorRepository.save(instructor);
            } else {
                throw new UniqueDBFieldException();
            }
        } else {
            throw new ActionNotAllowedException();
        }
    }

    @Transactional
    public Instructor edit(Instructor newInstructor, Long id) {
        Instructor instructorFromDB = getInstructorById(id);
        if (newInstructor.getEmail().equals(instructorFromDB.getEmail())) {
            newInstructor.setId(instructorFromDB.getId());
            return instructorRepository.save(newInstructor);
        } else {
            if (!isInstructorEmailAlreadyInDB(newInstructor)) {
                newInstructor.setId(instructorFromDB.getId());
                return instructorRepository.save(newInstructor);
            } else {
                throw new UniqueDBFieldException();
            }
        }
    }

    private Boolean isInstructorEmailAlreadyInDB(Instructor instructor) {
        return instructorRepository.existsByEmailIgnoreCase(instructor.getEmail());
    }

    @Override
    @Transactional
    public Boolean deleteById(Long id) {
        Instructor instructor = getInstructorById(id);
        instructorRepository.delete(instructor);
        return true;
    }

    public List<Instructor> findByEmail(String email) {
        return instructorRepository.findAllByEmailIsContainingIgnoreCase(email);
    }

    public List<Instructor> findByNames(String firstName, String lastName) {
        return instructorRepository
                .findAllByFirstNameIsContainingAndLastNameIsContainingAllIgnoreCase
                        (firstName, lastName);
    }

    @Transactional
    public Boolean sendEmailToInstructor(EmailDto emailData, Long id) {
        Instructor instructor = getInstructorById(id);
        emailSender.sendEmailToPerson(instructor, emailData);
        return true;
    }
}
