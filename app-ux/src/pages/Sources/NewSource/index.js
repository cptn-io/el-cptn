import { Fragment, useCallback } from "react";
import PageTitle from "../../../components/Nav/PageTitle";
import { useNavigate } from "react-router-dom";
import { breadcrumbs } from "..";
import CreateSource from "./CreateSource";
import ContextHelp from "../../../components/ContextHelp";

const NewSource = () => {
    const navigate = useNavigate();

    const onSuccess = useCallback((data) => {
        navigate(`/sources/${data.id}`);
    }, [navigate]);

    const onCancel = useCallback(() => {
        navigate('/sources');
    }, [navigate]);

    return <Fragment>
        <PageTitle itemKey="sources" label="New Source" breadcrumbs={breadcrumbs} />
        <div className="md:grid md:grid-cols-3 md:gap-6">
            <div className="mt-5 md:col-span-2 md:mt-0">
                <CreateSource onSuccess={onSuccess} onCancel={onCancel} />
            </div>
            <div className="md:col-span-1">
                <ContextHelp page="create-source" />
            </div>
        </div>
    </Fragment>
}

export default NewSource;