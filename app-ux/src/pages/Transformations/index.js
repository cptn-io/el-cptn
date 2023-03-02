import { Fragment } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { PlusIcon } from "@heroicons/react/24/solid";

const Transformations = () => {
    return <Fragment>
        <PageTitle itemKey="transformations">
            <button className="btn btn-primary btn-sm md:btn-md"><PlusIcon className="h-4 w-4 mr-2" /> New Transformation</button>
        </PageTitle>
    </Fragment>
}

export default Transformations;