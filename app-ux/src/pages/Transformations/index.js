import { Fragment } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { IconCirclePlus } from '@tabler/icons-react';

const Transformations = () => {
    return <Fragment>
        <PageTitle itemKey="transformations">
            <button className="btn btn-primary btn-sm md:btn-md"><IconCirclePlus size={24} className="mr-2" /> New Transformation</button>
        </PageTitle>
    </Fragment>
}

export default Transformations;