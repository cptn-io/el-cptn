import PipelineDetailsCard from "./PipelineDetailsCard";

const PipelineOverview = (props) => {
    const { data } = props;
    return <div className="grid grid-flow-row-dense grid-cols-1 xl:grid-cols-8 gap-4 ">
        <div className="xl:col-span-6">
            <PipelineDetailsCard data={data} />
        </div>
        <div className="hidden xl:col-span-2 xl:block m-2">
            <div className="card bg-base-100 shadow">
                <div className="card-body">
                    <h2 className="card-title">Helpful information</h2>
                    <div>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                    </div>
                </div>
            </div>
        </div>
    </div>
}

export default PipelineOverview;