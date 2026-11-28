
/**
* Generated class for Errors Aspect (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithErrorCollection).
*  The Errors Aspect delivers a list of the currently active errors for a specific machine.
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/


import { CollectionAspect,} from './esmf/aspect-meta-model/collectionAspect';
import { Error,} from './Error';

        export class AspectWithErrorCollection extends CollectionAspect<Error[],Error> {
    // NotNull
    items: Error[];
}

