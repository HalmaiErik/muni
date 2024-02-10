import { Box, Button, Card, IconButton, Modal, Typography } from "@mui/material";
import { CategoryDto } from "../../api/dtos";
import CategoryForm from "../category-form/CategoryForm";
import { useState } from "react";
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import EditNoteIcon from '@mui/icons-material/EditNote';
import { categorizeAccountTransactions } from "../../api/bank-account-data-api";

type Props = {
    categories: CategoryDto[];
    accountExternalId?: string;
};

const CategoryList = ({ categories, accountExternalId }: Props) => {
    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState<CategoryDto>();

    const openCreateModal = () => setCreateModalOpen(true);
    const closeCreateModal = () => setCreateModalOpen(false);

    const openEditModal = (category: CategoryDto) => {
        setSelectedCategory(category);
        setEditModalOpen(true);
    };
    const closeEditModal = () => setEditModalOpen(false);

    const categorizeTransactions = (categoryId?: number) => {
        if (accountExternalId && categoryId) {
            categorizeAccountTransactions({
                accountExternalId,
                categoryId
            });
        }
    };

    return (
        <>
            <div style={{ display: 'flex', marginBottom: '32px', maxHeight: 256, overflow: 'auto' }}>
                {categories.map((category, index) =>
                    <Card sx={{ maxWidth: 'fit-content', backgroundColor: `${category.colorCode}`, display: 'flex', alignItems: 'center', margin: '8px' }} key={index}>
                        <Typography sx={{ paddingLeft: '10px' }} variant="button">{category.name}</Typography>

                        <IconButton size="small" onClick={() => categorizeTransactions(category.id)}>
                            <PlayArrowIcon fontSize="inherit" />
                        </IconButton>

                        <IconButton size="small" onClick={() => openEditModal(category)}>
                            <EditNoteIcon fontSize="inherit" />
                        </IconButton>

                        <Modal open={editModalOpen} onClose={closeEditModal}>
                            <Box sx={{ height: '512px', minWidth: '350px', width: '70%', padding: 3, position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', bgcolor: 'background.paper' }}>
                                <CategoryForm category={selectedCategory} discard={closeEditModal} />
                            </Box>
                        </Modal>
                    </Card>
                )
                }
                <Button variant="text" size="small" onClick={openCreateModal}>Create category</Button>
            </div>

            <Modal open={createModalOpen} onClose={closeCreateModal}>
                <Box sx={{ height: '512px', minWidth: '350px', width: '70%', padding: 3, position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', bgcolor: 'background.paper' }}>
                    <CategoryForm discard={closeCreateModal} />
                </Box>
            </Modal>
        </>
    );
};

export default CategoryList;