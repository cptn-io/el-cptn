import { Link } from "react-router-dom";
import PageTitle from "../../components/Nav/PageTitle";
import { IconApps, IconBox } from "@tabler/icons-react";

const data = [
    {
        id: 1,
        title: "Slack",
        description: "Send message to a channel",
        created_by: 'foo',
        logo: '/temp/slack.svg'
    },
    {
        id: 2,
        title: "Teams",
        description: "Send message to a channel",
        created_by: 'foo',
        logo: '/temp/teams.png',
        alreadyInstalled: true
    },
    {
        id: 3,
        title: "ServiceNow",
        description: "Create/Update an incident",
        created_by: 'foo',
        logo: '/temp/now.png'
    },
    {
        id: 4,
        title: "PagerDuty",
        description: "Send an alert",
        created_by: 'foo',
        logo: '/temp/pd.png'
    },
    {
        id: 5,
        title: "Sendgrid",
        description: "Send an email",
        created_by: 'foo',
        logo: '/temp/sendgrid.png'
    },
    {
        id: 6,
        title: "Google Cloud Storage",
        description: "Write events to a bucket",
        created_by: 'foo',
        logo: '/temp/gcp.jpeg'
    },
    {
        id: 7,
        title: "AWS S3",
        description: "Write events to a bucket",
        created_by: 'foo',
        logo: '/temp/aws.png'
    },
    {
        id: 8,
        title: "MySQL",
        description: "Write to a table",
        created_by: 'foo',
        logo: '/temp/mysql.jpg'
    },
    {
        id: 9,
        title: "PostgreSQL",
        description: "Write to a table",
        created_by: 'foo',
        logo: '/temp/postgres.png'
    },
];

const App = ({ data }) => <div class="card bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
    <figure className="py-4" style={{ height: '160px', overflow: 'hidden' }}>{data.logo ? <img src={data.logo} height={128} width={128} alt={data.name} /> : <IconBox size={128} />}</figure>
    <div class="card-body bg-base-200 p-4">
        <h2 class="card-title">{data.title}</h2>
        <p>{data.description}</p>
        <div class="card-actions justify-end">
            <button disabled={data.alreadyInstalled} class="btn btn-primary">{data.alreadyInstalled ? 'Downloaded' : 'Download'}</button>
        </div>
    </div>
</div>



const Community = () => {
    return <>
        <PageTitle itemKey="community" />
        <div className="mt-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {data.map((item) => <App data={item} />)}
        </div>
    </>
}

export default Community;