import { Fragment } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { IconCirclePlus } from '@tabler/icons-react';

const Destinations = () => {
    return <Fragment>
        <PageTitle itemKey="destinations">
            <button className="btn btn-primary btn-sm md:btn-md"><IconCirclePlus size={24} className="mr-2" /> New Destination</button>
        </PageTitle>
    </Fragment>
}

export default Destinations;