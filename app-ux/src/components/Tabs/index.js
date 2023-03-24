import { Link } from "react-router-dom";

const Tabs = (props) => {
    const { tabs, activeTab, onTabChange } = props;

    const changeTab = (tabKey) => {
        if (onTabChange) {
            onTabChange(tabKey);
        }
    }
    return <div className="tabs bg-base-200 rounded">
        {tabs.map(tab => {
            return <button key={tab.key} className={`tab tab-lg tab-lifted ${tab.key === (activeTab || tabs?.[0]?.key) && 'tab-active'}`} onClick={() => changeTab(tab.key)}>{tab.label}</button>
        })}
    </div>
}

export default Tabs;