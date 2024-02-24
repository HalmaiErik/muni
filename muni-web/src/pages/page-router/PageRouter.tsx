import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import Account from "../account/Account";
import Accounts from "../accounts/Accounts";
import Home from "../home/Home";
import ProtectedPage, { ProtectedPageProps } from "../protected-page/ProtectedPage";

const PageRouter = () => {
    const { isAuthenticated } = useAuth();
    const DEFAULT_PROTECTED_PAGE_PROPS: Omit<ProtectedPageProps, 'outlet'> = {
        isAuthenticated,
        authenticationPath: '/',
    };

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/accounts" element={<ProtectedPage {...DEFAULT_PROTECTED_PAGE_PROPS} outlet={<Accounts />} />} />
                <Route path="/accounts/:accountExternalId" element={<ProtectedPage {...DEFAULT_PROTECTED_PAGE_PROPS} outlet={<Account />} />} />
            </Routes>
        </Router>
    );
}

export default PageRouter;