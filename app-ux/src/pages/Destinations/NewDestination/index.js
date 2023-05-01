import { Fragment, useCallback } from "react";
import PageTitle from "../../../components/Nav/PageTitle";
import { useNavigate } from "react-router-dom";
import { breadcrumbs } from "..";
import CreateDestination from "./CreateDestination";
import ContextHelp from "../../../components/ContextHelp";


const NewDestination = () => {
    const navigate = useNavigate();

    const onSuccess = useCallback((data) => {
        navigate(`/destinations/${data.id}`);
    }, [navigate])

    const onCancel = useCallback(() => {
        navigate('/destinations');
    }, [navigate]);

    return <Fragment>
        <PageTitle itemKey="destinations" label="New Destination" breadcrumbs={breadcrumbs} />
        <div className="md:grid md:grid-cols-3 md:gap-6">
            <div className="mt-5 md:col-span-2 md:mt-0">
                <CreateDestination onSuccess={onSuccess} onCancel={onCancel} />
            </div>
            <div className="md:col-span-1">
                <ContextHelp page="create-destination" />
            </div>
        </div>
    </Fragment>
}

export default NewDestination;