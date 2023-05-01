export const content = {
    'create-pipeline': {
        'title': 'Create a Pipeline',
        'body': `A pipeline donotes flow of data from source to destination. A pipeline can have multiple transformations in between.
            \nPipelines can be used to move data from one system to another, or to enrich data with additional information, or to remove sensitive information from the event payload.
            \nPipelines can be scheduled to run at a specific time or can be triggered to run for individual events.`
    },
    'pipeline-details': {
        'title': 'Pipeline Details',
        'body': `A pipeline donotes flow of data from source to destination. A pipeline can have multiple transformations in between. Use the Editor to change the flow of events in the Pipeline.
            \nInstead of processing standalone events, enable Scheduled Batch Processing to process events in batches at regular intervals (e.g. every 5 minutes).
            \nPipelines can be used to move data from one system to another, or to enrich data with additional information, or to remove sensitive information from the event payload.`
    },
    'create-source': {
        'title': 'Create a Source',
        'body': `A source donotes the point of data ingestion into the system for processing.An ingested event can be processed by multiple pipelines to the same event can be replicated or sent to multiple destinations
        \nA unique HTTP endpoint with an optional security key is generated for each source. Events can be sent to this endpoint to be ingested into the system.`
    },
    'source-details': {
        'title': 'Source Details',
        'body': `Use the Event URL for sending events into the system. The Inbound events will be sent to all associated Pipelines for the Source.
        \nClick 'Rotate keys' to generate a new primary key for the source. The old key will be set as Secondary key and will continue to work until the key is rotated again.`
    },
    'create-destination': {
        'title': 'Create a Destination',
        'body': `A destination is the terminal point of a pipeline for sending the processed event to.
        \nA destination is a Javascript module with execute function that is called with the event payload. New Destinations can be defined manually or can also be created from existing Apps. 
        \nOptionally, the destination module may also have setup and teardown methods. These methods are especially useful while processing events in scheduled batches`
    },
    'destination-details': {
        'title': 'Destination Details',
        'body': `A destination is the terminal point of a pipeline for sending the processed event to.
        \nA destination is a Javascript module with execute function that is called with the event payload. New Destinations can be defined manually or can also be created from existing Apps. 
        \nOptionally, the destination module may also have setup and teardown methods. These methods are especially useful while processing events in scheduled batches`
    },
    'create-transformation': {
        'title': 'Create a Transformation',
        'body': `Transformations help with enriching and removing sensitive data from your event payload. Add any Javascript code with required npm JS modules to transform your payloads.
        \nRemember that Transformations must return updated event payload for the event data to move across all the Pipeline steps. Pipeline will stop processing an event if a Transformation does not return the event object.`
    },
    'transformation-details': {
        'title': 'Transformation Details',
        'body': `Transformations help with enriching and removing sensitive data from your event payload. Add any Javascript code with required npm JS modules to transform your payloads.
        \nRemember that Transformations must return updated event payload for the event data to move across all the Pipeline steps. Pipeline will stop processing an event if a Transformation does not return the event object.`
    },
    'create-user': {
        'title': 'Create a User',
        'body': `Users can be created to allow access to the system. All users are treated as administrators and have full access to all the features of the system.
        \nAny user can update other users' passwords and revoke access to the system.`
    },
    'user-details': {
        'title': 'User Details',
        'body': `Click 'Edit User' to update user details. Changing the email address also changes the username for the user.
        \nClick 'Change Password' to update the password for the user.`
    }
}