import { useNavigate, useParams } from "react-router-dom";
import PageTitle from "../../components/Nav/PageTitle";
import Tabs from "../../components/Tabs";
import SSO from "./SSO";
import ContextHelp from "../../components/ContextHelp";

const tabs = [{ 'key': 'sso', label: 'Single Sign On' }];

const Settings = () => {
    const navigate = useNavigate();

    const { tab } = useParams();

    const onTabChange = (tab) => {
        navigate(`/settings/${tab}`);
    }

    return (
        <>
            <PageTitle itemKey="settings" />
            <Tabs tabs={tabs} activeTab={tab} onTabChange={onTabChange} />
            {(!tab || tab === 'sso') &&
                <div className="grid grid-flow-row-dense grid-cols-1 xl:grid-cols-8 gap-4 ">
                    <div className="xl:col-span-6 mt-4">
                        <SSO />
                    </div>
                    <div className="hidden xl:col-span-2 xl:block">
                        <ContextHelp page="sso-help" />
                    </div>
                </div>
            }
        </>
    );
}

export default Settings;