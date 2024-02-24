import { Navigate } from "react-router-dom";

type ProtectedPageProps = {
    isAuthenticated: boolean;
    authenticationPath: string;
    outlet: JSX.Element;
};

const ProtectedPage = ({ isAuthenticated, authenticationPath, outlet }: ProtectedPageProps) => {
    if (isAuthenticated) {
        return outlet;
    }
    else {
        return <Navigate to={{ pathname: authenticationPath }} />;
    }
};

export default ProtectedPage;
export type { ProtectedPageProps };
