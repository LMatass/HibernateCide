
import Entity.DepartmentEntity;
import Entity.PersonEntity;
import Entity.ProfessorEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    static SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    static Session session = sessionFactory.openSession();

    public static void main(String[] args) {
        handleMenu();
    }

    public static void printMenu() {
        System.out.println("\nBienvenido al programa de gestion del CIDE\n" +
                "Seleccione una de las opciones\n" +
                "1. Consultar una persona/profesor/departamento\n" +
                "2. Dar de alta una persona/profesor/departamento\n" +
                "3. Dar de baja una persona/profesor/departamento\n" +
                "4. Modificar una persona/profesor/departamento\n" +
                "5. Lista de personas de manera ordenada");
    }

    public static void handleMenu() {
        Scanner scInt = new Scanner(System.in);
        printMenu();

        int input = scInt.nextInt();

        switch (input) {
            case 1:
                consult();
                break;
            case 2:
                addEntities();
                break;
            case 3:
                removeEntities();
                break;
            case 4:
                modifyEntities();
                break;
            case 5:
                sortEntities();
                break;
            default:
                System.out.println("Valor incorrecto, introduzca un numero entre 1 y 6");
        }


    }

    public static void sortEntities() {
        Scanner scInt = new Scanner(System.in);
        System.out.println("Has elegido la opcion de listar las personas de manera ordenada\n" +
                "¿De que manera quieres ordenar los resultados?\n" +
                "1. Nombre\n" +
                "2. 1er apellido\n" +
                "3. 2do appellido\n" +
                "4. Fecha de nacimiento");

        int input = scInt.nextInt();
        switch (input) {
            case 1:
                sort(1);
                break;
            case 2:
                sort(2);
                break;
            case 3:
                sort(3);
                break;
            case 4:
                sort(4);
                break;
            default:
                System.out.println("Valor incorrecto, introduzca un numero entre 1 y 3");
        }
    }

    public static void sort(int i) {
        try {
            session.beginTransaction();

            Query query = session.createQuery("From PersonEntity p");
            List<PersonEntity> p = query.list();
            Collections.sort(p, new Comparator<PersonEntity>() {
                @Override
                public int compare(PersonEntity p1, PersonEntity p2) {
                    switch (i) {
                        case 1:
                            return p1.getName().compareTo(p2.getName());
                        case 2:
                            return p1.getFirstName().compareTo(p2.getFirstName());
                        case 3:
                            return p1.getLastName().compareTo(p2.getLastName());
                        case 4:
                            return p1.getBirthDate().compareTo(p2.getBirthDate());
                    }
                    return 0;
                }
            });

            for (PersonEntity personEntity : p) {
                System.out.println(personEntity.toString());
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    public static void modifyEntities() {
        Scanner scInt = new Scanner(System.in);
        System.out.println("Has elegido la opcion de modificar una entidad\n" +
                "Quieres modificar... :\n" +
                "1. Persona\n" +
                "2. Profesor\n" +
                "3. Departamento\n");

        int input = scInt.nextInt();
        switch (input) {
            case 1:
                modifyPerson();
                break;
            case 2:
                modifyProfessor();
                break;
            case 3:
                modifyDepartment();
                break;
            default:
                System.out.println("Valor incorrecto, introduzca un numero entre 1 y 3");
        }
    }

    public static void modifyPerson() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Indica el id de la persona a modificar");
        int id = sc.nextInt();

        try {
            session.beginTransaction();

            Query query = session.createQuery("from PersonEntity where id = " + id);
            PersonEntity personEntity = (PersonEntity) query.uniqueResult();

            System.out.println("Modificando valores de " + personEntity.getName() + ": ");
            // Recolectando los datos introducidos por el usuario
            String nif = askForInfo("el NIF: ");
            String name = askForInfo("el nombre: ");
            String firstName = askForInfo("el 1er apellido: ");
            String lastName = askForInfo("el 2do apellido: ");
            String birthDate = askForInfo("la fecha de nacimiento en formato (yyyy-mm-dd): ");
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
            String address = askForInfo("la direccion: ");
            String gender = askForInfo("el sexo de la persona: ");
            String phoneNumber = askForInfo("el numero de telefono: ");
            // Accediendo al objeto Person y modiciando sus valores mediante los metodos Set()
            PersonEntity p = session.load(PersonEntity.class, personEntity.getId());
            p.setNif(nif);
            p.setName(name);
            p.setFirstName(firstName);
            p.setLastName(lastName);
            p.setBirthDate(date);
            p.setAddress(address);
            p.setGender(gender);
            p.setPhoneNumber(Integer.valueOf(phoneNumber));

            session.update(p);
            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void modifyProfessor() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Indica el id del profesor a modificar");
        int id = sc.nextInt();

        try {
            session.beginTransaction();

            Query query = session.createQuery("from ProfessorEntity where id = " + id);
            ProfessorEntity professorEntity = (ProfessorEntity) query.uniqueResult();

            System.out.println("Modificando valores del profesor con id " + professorEntity.getId() + ": ");

            String departmentId = askForInfo("el id del nuevo departamento: ");

            ProfessorEntity p = session.load(ProfessorEntity.class, professorEntity.getId());
            p.setDepartmentId(Integer.parseInt(departmentId));

            session.update(p);
            session.getTransaction().commit();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void modifyDepartment() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Indica el id del departamento a modificar");
        int id = sc.nextInt();

        try {
            session.beginTransaction();

            Query query = session.createQuery("from DepartmentEntity where id = " + id);
            DepartmentEntity departmentEntity = (DepartmentEntity) query.uniqueResult();

            System.out.println("Modificando valores del departamento con nombre actual '" + departmentEntity.getName() + "': ");

            String name = askForInfo("el nuevo nombre del departamento: ");

            DepartmentEntity d = session.load(DepartmentEntity.class, departmentEntity.getId());
            d.setName(name);

            session.update(d);
            session.getTransaction().commit();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static String askForInfo(String valueToGet) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce " + valueToGet);
        return sc.nextLine();
    }

    public static void removeEntities() {
        Scanner scInt = new Scanner(System.in);
        System.out.println("Has elegido la opcion de eliminar una entidad\n" +
                "Quieres eliminar... :\n" +
                "1. Persona\n" +
                "2. Profesor\n" +
                "3. Departamento\n");

        int input = scInt.nextInt();
        switch (input) {
            case 1:
                removePerson();
                break;
            case 2:
                removeProfessor();
                break;
            case 3:
                removeDepartment();
                break;
            default:
                System.out.println("Valor incorrecto, introduzca un numero entre 1 y 3");
        }
    }

    public static void removePerson() {
        Scanner scString = new Scanner(System.in);
        System.out.println("Indica el id");
        int id = scString.nextInt();

        try {
            session.beginTransaction();
            PersonEntity personEntity = session.get(PersonEntity.class, id);
            session.delete(personEntity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void removeProfessor() {
        Scanner scString = new Scanner(System.in);
        System.out.println("Indica el id");
        int id = scString.nextInt();

        try {
            session.beginTransaction();
            ProfessorEntity professorEntity = session.get(ProfessorEntity.class, id);
            session.delete(professorEntity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void removeDepartment() {
        Scanner scString = new Scanner(System.in);
        System.out.println("Indica el id");
        int id = scString.nextInt();

        try {
            session.beginTransaction();
            DepartmentEntity departmentEntity = session.get(DepartmentEntity.class, id);
            session.delete(departmentEntity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void consult() {
        Scanner scInt = new Scanner(System.in);
        System.out.println("Has elegido la opcion de consultar\n" +
                "Quieres consultar sobre:\n" +
                "1. Persona\n" +
                "2. Departamento\n");

        int input = scInt.nextInt();
        switch (input) {
            case 1:
                consultPersons();
                break;
            case 2:
                consultDepartaments();
                break;
            default:
                System.out.println("Valor incorrecto, introduzca un numero entre 1 y 3");
        }
    }

    public static void consultPersons() {
        Scanner scInt = new Scanner(System.in);
        System.out.println("Has elegido la opcion de consultar personas\n" +
                "Quieres consultar usando:\n" +
                "1. ID\n" +
                "2. Nombre\n" +
                "3. Primer apellido\n" +
                "4. Segundo apellido\n" +
                "5. NIF");

        int input = scInt.nextInt();
        switch (input) {
            case 1:
                consultPersonsQuery("id");
                break;
            case 2:
                consultPersonsQuery("name");
                break;
            case 3:
                consultPersonsQuery("first_name");
                break;
            case 4:
                consultPersonsQuery("final_name");
                break;
            case 5:
                consultPersonsQuery("nif");
                break;
            default:
                System.out.println("Valor incorrecto, introduzca un numero entre 1 y 4");
        }
    }

    public static void consultPersonsQuery(String input) {
        Scanner scString = new Scanner(System.in);
        System.out.println("Indica el " + input);
        String valueToSearch = scString.nextLine();
        String professorId = "";
        String departmentId = "";
        String departmentName = "";
        try {
            session.beginTransaction();
            Query query = session.createQuery("from PersonEntity where " + input + " = '" + valueToSearch + "'");
            // Recorriendo la lista de objetos Person
            List<PersonEntity> personEntities = query.list();
            for (PersonEntity person : personEntities) {
                int personId = person.getId();
                query = session.createQuery("from ProfessorEntity where personId = " + personId);

                ProfessorEntity p = (ProfessorEntity) query.uniqueResult();
                if (p != null){
                    professorId = String.valueOf(p.getId());
                    departmentId = String.valueOf(p.getDepartmentId());

                    query = session.createQuery("from DepartmentEntity where id = " + departmentId);
                    DepartmentEntity d = (DepartmentEntity) query.uniqueResult();
                    departmentName = d.getName();
                }


                System.out.println(person.toString() +
                        "\nId profesor: " + professorId +
                        "\nId departamento: " + departmentId +
                        "\nNombre departamento: " + departmentName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void consultDepartaments() {
        Scanner scInt = new Scanner(System.in);
        System.out.println("Has elegido la opcion de consultar departamentos\n" +
                "Quieres consultar usando:\n" +
                "1. ID\n" +
                "2. Nombre\n");
        int input = scInt.nextInt();
        switch (input) {
            case 1:
                consultDepartmentsQuery("id");
                break;
            case 2:
                consultDepartmentsQuery("name");
                break;
            default:
                System.out.print("Valor incorrecto, introduzca un numero entre 1 y 2");
        }
    }

    public static void consultDepartmentsQuery(String input) {
        Scanner scString = new Scanner(System.in);
        System.out.println("Indica el " + input);
        String valueToSearch = scString.nextLine();
        try {
            session.beginTransaction();
            Query query = session.createQuery("from DepartmentEntity where " + input + " = '" + valueToSearch + "'");

            DepartmentEntity d = (DepartmentEntity) query.uniqueResult();
            String departmentName = d.getName();
            String departmentIdS = String.valueOf(d.getId());

            System.out.println("Resultado: \n" +
                    "Nombre del departamento: " + departmentName +
                    "\nId del departamento: " + departmentIdS);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void addEntities() {
        Scanner scInt = new Scanner(System.in);
        System.out.println("Has elegido la opcion de añadir una entidad\n" +
                "Quieres añadir... :\n" +
                "1. Persona\n" +
                "2. Profesor\n" +
                "3. Departamento\n");

        int input = scInt.nextInt();
        switch (input) {
            case 1:
                addPerson();
                break;
            case 2:
                addProfessor();
                break;
            case 3:
                addDepartment();
                break;
            default:
                System.out.println("Valor incorrecto, introduzca un numero entre 1 y 3");
        }
    }

    public static void addPerson() {
        Scanner sc = new Scanner(System.in);
        try {
            session.beginTransaction();
            PersonEntity p = new PersonEntity();

            String nif = askForInfo("el NIF: ");
            String name = askForInfo("el nombre: ");
            String firstName = askForInfo("el 1er apellido: ");
            String lastName = askForInfo("el 2do apellido: ");
            String birthDate = askForInfo("la fecha de nacimiento en formato (yyyy-mm-dd): ");
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
            String address = askForInfo("la direccion: ");
            String gender = askForInfo("el sexo de la persona: ");
            String phoneNumber = askForInfo("el numero de telefono: ");

            p.setNif(nif);
            p.setName(name);
            p.setFirstName(firstName);
            p.setLastName(lastName);
            p.setBirthDate(date);
            p.setAddress(address);
            p.setGender(gender);
            p.setPhoneNumber(Integer.valueOf(phoneNumber));

            session.saveOrUpdate(p);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void addProfessor() {
        Scanner sc = new Scanner(System.in);
        try {
            session.beginTransaction();
            ProfessorEntity professorEntity = new ProfessorEntity();

            System.out.println("Inserta el id de persona");
            int personId = sc.nextInt();
            professorEntity.setPersonId(personId);

            System.out.println("Inserta el id del departamento");
            int departmentId = sc.nextInt();
            professorEntity.setDepartmentId(departmentId);

            System.out.print("Secuencia realizada correctamente");

            session.saveOrUpdate(professorEntity);
            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void addDepartment() {
        Scanner sc = new Scanner(System.in);
        try {
            session.beginTransaction();
            DepartmentEntity departmentEntity = new DepartmentEntity();

            System.out.println("Inserta el nombre");
            String name = sc.nextLine();
            departmentEntity.setName(name);

            session.saveOrUpdate(departmentEntity);
            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

}
