import { Fragment, useEffect, useState } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { PlusIcon, ChevronDoubleLeftIcon, ChevronDoubleRightIcon, ChevronLeftIcon, ChevronRightIcon } from "@heroicons/react/24/solid";
import { Link } from "react-router-dom";
import axios from "axios";
import get from "lodash/get";
import useNotifications from "../../hooks/useNotifications";

const Sources = () => {
    const [loading, setLoading] = useState(true);
    const { addNotification } = useNotifications();

    useEffect(() => {

        axios.get('/api/source').then(response => {
            console.log(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Sources'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, []);

    return <Fragment>
        <PageTitle itemKey="sources">
            <Link to="/app/sources/new" className="btn btn-primary btn-sm md:btn-md"><PlusIcon className="h-4 w-4 mr-2" /> New Source</Link>
        </PageTitle>
        <div className="overflow-x-auto">
            <table className="table table-zebra w-full">
                {/* head */}
                <thead>
                    <tr>
                        <th></th>
                        <th>Name</th>
                        <th>Secured</th>
                        <th>Active</th>
                    </tr>
                </thead>
                <tbody>
                    {/* row 1 */}
                    <tr>
                        <th>1</th>
                        <td>Cy Ganderton</td>
                        <td>Quality Control Specialist</td>
                        <td>Blue</td>
                    </tr>
                    {/* row 2 */}
                    <tr>
                        <th>2</th>
                        <td>Hart Hagerty</td>
                        <td>Desktop Support Technician</td>
                        <td>Purple</td>
                    </tr>
                    {/* row 3 */}
                    <tr>
                        <th>3</th>
                        <td>Brice Swyre</td>
                        <td>Tax Accountant</td>
                        <td>Red</td>
                    </tr>
                </tbody>
            </table>
            <div className="mt-3 grid grid-cols-1 justify-items-end">
                <div className="btn-group">
                    <button className="btn btn-outline btn-sm"><ChevronDoubleLeftIcon className="h-6 w-6" /></button>
                    <button className="btn btn-outline btn-sm"><ChevronLeftIcon className="h-6 w-6" /></button>
                    <button className="btn btn-outline btn-sm"><ChevronRightIcon className="h-6 w-6" /></button>
                    <button className="btn btn-outline btn-sm"><ChevronDoubleRightIcon className="h-6 w-6" /></button>
                </div>
            </div>
        </div>
    </Fragment>
}

export default Sources;