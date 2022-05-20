package CRUD.Controller;

import CRUD.Controller.Person.Persona;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IPersonaRepoImpl {
    @PersistenceContext
    private EntityManager entityManager;


    public List<Persona> getData(HashMap<String,Object> conditions) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Persona> criteriaQuery = cb.createQuery(Persona.class);
        Root<Persona> root = criteriaQuery.from(Persona.class);

        List<Predicate> predicates = new ArrayList<>();
        conditions.forEach((field,value) ->
        {
            switch (field) {
                case "user", "name", "surname" -> predicates.add(cb.like(root.get(field), "%" + (String) value + "%"));
                case "created_date" -> {
                    String dateCondition = (String) conditions.get("dateCondition");
                    switch (dateCondition) {
                        case "GREATER_THAN":
                            try {
                                predicates.add((Predicate) cb.greaterThan(root.<Date>get(field), (Date) new SimpleDateFormat("yyyy-MM-dd").parse((String) value)));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "LESS_THAN":
                            try {
                                predicates.add((Predicate) cb.lessThan(root.<Date>get(field), (Date) new SimpleDateFormat("yyyy-MM-dd").parse((String) value)));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "EQUAL":
                            try {
                                predicates.add((Predicate) cb.equal(root.<Date>get(field), (Date) new SimpleDateFormat("yyyy-MM-dd").parse((String) value)));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                    }
                    break;
                }
            }
        });
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        int first = Integer.parseInt((String) conditions.get("pageIndex"));
        int max = 10;
        if(conditions.containsKey("pageSize")){
            max= Integer.parseInt((String) conditions.get("pageSize"));
        }


        return entityManager.createQuery(criteriaQuery).setFirstResult(first).setMaxResults(max).getResultList();

    }

}