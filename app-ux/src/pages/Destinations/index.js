import { Fragment } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { PlusIcon } from "@heroicons/react/24/solid";

const Destinations = () => {
    return <Fragment>
        <PageTitle itemKey="destinations">
            <button className="btn btn-primary btn-sm md:btn-md"><PlusIcon className="h-4 w-4 mr-2" /> New Destination</button>
        </PageTitle>
    </Fragment>
}

export default Destinations;