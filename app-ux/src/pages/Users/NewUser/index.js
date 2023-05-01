import { useNavigate } from "react-router-dom";
import CreateUser from "./CreateUser";
import { useCallback } from "react";
import PageTitle from "../../../components/Nav/PageTitle";
import { breadcrumbs } from "..";
import ContextHelp from "../../../components/ContextHelp";

const NewUser = () => {
    const navigate = useNavigate();

    const onSuccess = useCallback((data) => {
        navigate(`/users/${data.id}`);
    }, [navigate]);

    const onCancel = useCallback(() => {
        navigate('/users');
    }, [navigate]);

    return <>
        <PageTitle itemKey="users" label="New User" breadcrumbs={breadcrumbs} />
        <div className="md:grid md:grid-cols-3 md:gap-6">
            <div className="mt-5 md:col-span-2 md:mt-0">
                <CreateUser onSuccess={onSuccess} onCancel={onCancel} />
            </div>
            <div className="md:col-span-1">
                <ContextHelp page='create-user' />
            </div>
        </div>
    </>
}

export default NewUser;