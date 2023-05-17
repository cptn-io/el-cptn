import { Helmet } from "react-helmet";

const UnknownError = () => {

    return <div className="w-4/5 mx-auto bg-base-300 rounded-lg overflow-hidden md:w-2/4 lg:w-1/4">
        <Helmet>
            <title>Unknown Error | cptn.io</title>
        </Helmet>
        <div className="p-5">
            <div className="mb-4">
                <h2 className="text-lg font-semibold uppercase">Unknown Error</h2>
            </div>
            <div>
                An error occurred while performing this request. Please ensure the URL you are trying to use is valid.
            </div>
        </div>
    </div>
}

export default UnknownError;