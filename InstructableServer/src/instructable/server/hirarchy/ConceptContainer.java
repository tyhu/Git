package instructable.server.hirarchy;

import java.util.*;

/**
 * Created by Amos Azaria on 21-Apr-15.
 *
 * This class has access to all defined concepts and their fields and types.
 * This class has the required information for creating an instance of a concept.
 * This class can access the data base
 */
public class ConceptContainer
{
    Map<String,List<FieldDescription>> conceptFieldMap;

    public ConceptContainer()
    {
        conceptFieldMap = new HashMap<>();
        conceptFieldMap.put("email", Arrays.asList(EmailMessage.fieldDescriptions));
    }

    public boolean doesConceptExist(String concept)
    {
        return conceptFieldMap.containsKey(concept);
    }

    public boolean doesFieldExistInConcept(String concept, String fieldName)
    {
        if (conceptFieldMap.containsKey(concept))
        {
            List<FieldDescription> fieldDescriptionList = conceptFieldMap.get(concept);
            for (FieldDescription fieldDescription : fieldDescriptionList)
            {
                if (fieldDescription.fieldName.equals(fieldName))
                    return true;
            }
        }
        return false;
    }

    public List<String> findConceptsForField(String fieldName)
    {
        List<String> candidates = new LinkedList<>();
        for (String concept : conceptFieldMap.keySet())
        {
            List<FieldDescription> fieldDescriptionList = conceptFieldMap.get(concept);
            for (FieldDescription fieldDescription : fieldDescriptionList)
            {
                if (fieldDescription.fieldName.equals(fieldName))
                {
                    candidates.add(concept);
                    break;
                }
            }
        }
        return candidates;
    }
}
