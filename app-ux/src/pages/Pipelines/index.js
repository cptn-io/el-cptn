import { Fragment } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { IconCirclePlus } from '@tabler/icons-react';

const Pipelines = () => {
    return <Fragment>
        <PageTitle itemKey="pipelines">
            <button className="btn btn-primary btn-sm md:btn-md"><IconCirclePlus size={24} className="mr-2" /> New Pipeline</button>
        </PageTitle>
    </Fragment>
}

export default Pipelines;