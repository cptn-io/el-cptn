import { Fragment, useCallback } from "react";
import PageTitle from "../../../components/Nav/PageTitle";
import { useNavigate } from "react-router-dom";
import { breadcrumbs } from "..";
import CreateDestination from "./CreateDestination";


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
                <div className="px-4 sm:px-0">
                    <h3 className="text-base font-semibold leading-6">Create a new Destination</h3>
                    <p className="text-base-content mt-1 text-sm">
                        This information will be displayed publicly so be careful what you share.
                    </p>
                </div>
            </div>
        </div>
    </Fragment>
}

export default NewDestination;