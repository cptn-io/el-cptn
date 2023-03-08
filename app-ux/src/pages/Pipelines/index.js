import { Fragment, useState } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { IconCirclePlus } from '@tabler/icons-react';
import ItemSelectorModal from "../../components/ItemSelectorModal";
import { Link } from "react-router-dom";

const Pipelines = () => {
    const [showModal, setShowModal] = useState(false);
    return <Fragment>
        <PageTitle itemKey="pipelines">
            <Link to="/pipelines/new" className="btn btn-primary btn-sm md:btn-md"><IconCirclePlus size={24} className="mr-2" />New Pipeline</Link>
            {/* <button className="btn btn-primary btn-sm md:btn-md" onClick={() => setShowModal(true)}><IconCirclePlus size={24} className="mr-2" /> New Pipeline</button> */}
        </PageTitle>
        {/* {showModal && <ItemSelectorModal url="/api/source" title="Select a Source" cols={[{ "label": "Name", "datacss": "text-left", "css": "w-1/2", "name": "name" }, { "label": "Active", "datacss": "flex justify-center", "css": "text-center", "name": "active" }]} onCancel={() => setShowModal(false)} onSelect={(item) => { console.log(item); setShowModal(false) }} />} */}
        {showModal && <ItemSelectorModal url="/api/destination" title="Select a Destination" cols={[{ "label": "Name", "datacss": "text-left", "css": "w-1/2", "name": "name" }, { "label": "Active", "datacss": "flex justify-center", "css": "text-center", "name": "active" }]} onCancel={() => setShowModal(false)} onSelect={(item) => { console.log(item); setShowModal(false) }} />}
    </Fragment>
}

export const breadcrumbs = [{ label: 'Pipelines', url: '/pipelines' }];

export default Pipelines;